package org.venus.choosers;

import org.venus.ChooserBase;
import org.venus.Protocol;
import org.venus.config.FixedPoolConfig;
import org.venus.protocols.PooledProtocol;
import org.venus.protocols.dubbo.DubboProtocol;
import org.venus.protocols.grpc.GrpcProtocol;
import org.venus.protocols.http.HttpProtocol;

import java.util.HashMap;
import java.util.Map;

public final class ProtocolChooser extends ChooserBase {

    private final Map<String, PooledProtocol> protocols = new HashMap<>();

    public ProtocolChooser() {
        this(new FixedPoolConfig());
    }

    public ProtocolChooser(FixedPoolConfig cfg) {
        this.protocols.put(GrpcProtocol.NAME, new PooledProtocol(new GrpcProtocol(), cfg));
        this.protocols.put(HttpProtocol.NAME, new PooledProtocol(new HttpProtocol(), cfg));
        this.protocols.put(DubboProtocol.NAME, new PooledProtocol(new DubboProtocol(), cfg));
    }

    @Override
    protected Protocol doChoose(String name) {
        return protocols.get(name);
    }

}
