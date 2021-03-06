## Deploy Architecture

```
      │          ▲
      │          │
   request    response
      │          │
      ▼          │
┌───────────────────────┐
│ HTTP(S)/HTTP2/Binary  │
└───────────────────────┘
      │          ▲
      │          │
   request    response
      │          │
      ▼          │
┌───────────────────────┐             ┌───────────────────────┐
│     API  Gateway      │ ◀─────────▶ │    Gateway Console    │
└───────────────────────┘             └───────────────────────┘
      │          ▲
      │          │
   request    response
      │          │
      ▼          │
┌───────────────────────┐
│     Internal Apps     │
└───────────────────────┘
```

## Network Protocol Processing

```
               +------------------------------------- Network Process ----------------------------------------+
               |                                                                                              |
               |    ┌──── TCP ────┐                  ┌────────────┐               ┌─────────────┐             |      
─── request ───|──▶ │             │                  │            │               │             │ ───── req ──|──▶ 
               |    │   Acceptor  │ ──── socket ───▶ │  Executor  │ ─── adapt ──▶ │   Adapter   │             |      
◀── response ──|─── │             │                  │            │               │             │ ◀──── resp ─|───
               |    └─────────────┘                  └────────────┘               └─────────────┘             |
               |                                                                                              |
               +----------------------------------------------------------------------------------------------+

```

## Business Processing

```
           +--------------------------- InboundEngine --------------------------+              +--------------------------- RequestFilter --------------------------+
           |                                                                    |              |                                                                    |
           |    ┌──────────────────────── Pipeline ────────────────────────┐    |              |    ┌────────────────────── FilterChain ───────────────────────┐    |
─── req ───|──▶ │  ┌───────┐      ┌───────┐      ┌───────┐      ┌───────┐  │────|── req ───────|───▶│  ┌───────┐      ┌───────┐      ┌───────┐      ┌───────┐  │    |
           |    │  │ First │ ───▶ │ ..... │ ───▶ │ ..... │ ───▶ │ Basic │  │    |              |    │  │ First │ ───▶ │ ..... │ ───▶ │ ..... │ ───▶ │ Basic │  │    |
◀── resp ──|─── │  └───────┘      └───────┘      └───────┘      └───────┘  │◀───|── req ───────|────│  └───────┘      └───────┘      └───────┘      └───────┘  │    |
           |    └───────────────────────────────────────────────────│──────┘    |              |    └──────────────────────────────────────────────────────────┘    |
           |                                                        │           |              |                                                                    |
           +--------------------------------------------------------│-----------+              +--------------------------------------------------------------------+
                                                                    │
    ┌──────────────────────────── req ──────────────────────────────┘
    │
    │      +--------------------------- EndpointEngine -------------------------+              +------------------------- Call Internal Apps -----------------------+
    │      |                                                                    |              |                                                                    |
    │      |    ┌──────────────────────── Pipeline ────────────────────────┐    |              |    ┌──────────────────────────────────────────────────────────┐    |
    │      |    │  ┌───────┐      ┌───────┐      ┌───────┐      ┌───────┐  │────|── req ───────|───▶│  ┌────────┐                                    ┌──────┐  │    |
    └──────|──▶ │  │ First │ ───▶ │ ..... │ ───▶ │ ..... │ ───▶ │ Basic │  │    |              |    │  │ Client │ ─────────────────────────────────▶ │ Apps │  │    |
           |    │  └───────┘      └───────┘      └───────┘      └───────┘  │◀───|── resp ──────|────│  └────────┘                                    └──────┘  │    |
           |    └───────────────────────────────────────────────────│──────┘    |              |    └──────────────────────────────────────────────────────────┘    |
           |                                                        │           |              |                                                                    |
           +--------------------------------------------------------│-----------+              +--------------------------------------------------------------------+
                                                                    │
    ┌────────────────────────── req, resp ──────────────────────────┘
    │
    │      +--------------------------- OutboundEngine -------------------------+              +--------------------------- ResponseFilter -------------------------+
    │      |                                                                    |              |                                                                    |
    │      |    ┌──────────────────────── Pipeline ────────────────────────┐    |              |    ┌────────────────────── FilterChain ───────────────────────┐    |
    │      |    │  ┌───────┐      ┌───────┐      ┌───────┐      ┌───────┐  │────|── resp ──────|───▶│  ┌───────┐      ┌───────┐      ┌───────┐      ┌───────┐  │    |
    └──────|──▶ │  │ First │ ───▶ │ ..... │ ───▶ │ ..... │ ───▶ │ Basic │  │    |              |    │  │ First │ ───▶ │ ..... │ ───▶ │ ..... │ ───▶ │ Basic │  │    |
           |    │  └───────┘      └───────┘      └───────┘      └───────┘  │◀───|── resp ──────|────│  └───────┘      └───────┘      └───────┘      └───────┘  │    |
           |    └──────────────────────────────────────────────────────────┘    |              |    └──────────────────────────────────────────────────────────┘    |
           |                                                                    |              |                                                                    |
           +--------------------------------------------------------------------+              +--------------------------------------------------------------------+


```