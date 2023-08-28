package vn.vnpay.demo3.controller;

//@Slf4j
//@ChannelHandler.Sharable
//public class VerifyTokenController extends SimpleChannelInboundHandler<FullHttpRequest> {
//
//    private static VerifyTokenController instance;
//
//    public static VerifyTokenController getInstance() {
//        if (Objects.isNull(instance)) {
//            instance = new VerifyTokenController();
//        }
//        return instance;
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
//        HttpMethod method = request.method();
//        String uri = request.uri();
//        if (HttpMethod.POST.equals(method)) {
//            if ("/verify/accessToken".equals(uri)) {
//                log.info("Start request to verify AccessToken");
//                VerifyTokenControllerHandler.getInstance().handleVerifyAccessToken(ctx, request);
//            }
//        }
//    }
//}
