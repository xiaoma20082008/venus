package org.venus.choosers;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.ClientConnector;
import org.venus.Response;
import org.venus.SessionContext;
import org.venus.core.StandardHttpRequest;
import org.venus.nio.NioConnection;
import org.venus.protocols.http.HttpProtocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class ProtocolChooserTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolChooserTest.class);

    @Test
    public void test_choose() {
        ProtocolChooser chooser = new ProtocolChooser();
        NioConnection connection = new NioConnection(null);
        connection.context().put(SessionContext.PROTOCOL_KEY, HttpProtocol.NAME);
        StandardHttpRequest request = StandardHttpRequest.builder()
                .method("GET")
                .uri("/index")
                .version("HTTP/1.1")
                .connection(connection)
                .build();
        try (ClientConnector client = chooser.choose(request).create();) {
            Response response = client.invoke(request);
            LOGGER.info("request={},response={}", request, response);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Test
    public void test_rpc() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Thread t1 = new Thread(() -> {
            LOGGER.info("rpc provider started");
            RpcFramework.export(new HelloServiceImpl(), 5432);
        });
        t1.start();
        new Thread(() -> {
            try {
                HelloService impl = RpcFramework.refer(HelloService.class, "localhost", 5432);
                LOGGER.info("{}", impl.hello("Tom"));
            } finally {
                latch.countDown();
            }
        }).start();
        latch.await();
        t1.interrupt();
    }

    static class RpcFramework {
        public static void export(Object impl, int port) {
            try (ServerSocket server = new ServerSocket(port);) {
                while (!Thread.interrupted()) {
                    Socket client = server.accept();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                                 ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());) {
                                String method = ois.readUTF();
                                Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();
                                Object[] args = (Object[]) ois.readObject();
                                Method m = impl.getClass().getMethod(method, parameterTypes);
                                Object ret = m.invoke(impl, args);
                                oos.writeObject(ret);
                            } catch (Throwable e) {
                                LOGGER.error("", e);
                            }
                        }
                    }).start();
                }
            } catch (IOException ioe) {
                LOGGER.error("", ioe);

            }
        }

        public static <T> T refer(Class<T> api, String host, int port) {
            return api.cast(Proxy.newProxyInstance(RpcFramework.class.getClassLoader(), new Class[]{api}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    try (Socket socket = new Socket()) {
                        socket.connect(new InetSocketAddress(host, port));
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeUTF(method.getName());
                        oos.writeObject(method.getParameterTypes());
                        oos.writeObject(args);
                        return new ObjectInputStream(socket.getInputStream()).readObject();
                    }
                }
            }));
        }
    }

    static interface HelloService {
        String hello(String name);
    }

    static class HelloServiceImpl implements HelloService {
        @Override
        public String hello(String name) {
            return "hello " + name;
        }
    }
}
