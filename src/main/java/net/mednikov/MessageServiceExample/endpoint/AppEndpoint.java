package net.mednikov.MessageServiceExample.endpoint;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;

import net.mednikov.MessageServiceExample.auth.AuthProvider;
import net.mednikov.MessageServiceExample.message.MessageService;

public class AppEndpoint extends AbstractVerticle {

    private EventBus bus;
    private AuthProvider authProvider;

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AppEndpoint());
        vertx.deployVerticle(new MessageService());

    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        Future<Void> initSteps = initAuthProvider().compose(v->initServer());
        initSteps.setHandler(startFuture.completer());
    }

    private Future<Void> initServer(){
        Future<Void> fut = Future.future();
        //initialize web server and router
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        //Don't forget to get an eventbus reference!
        this.bus = vertx.eventBus();
        //secure routes with AuthProvider
        router.route("/secure/*").handler(JWTAuthHandler.create(authProvider));
        //get token
        router.get("/login").handler(this::getToken);
        //assign hanlder to routes
        router.get("/secure/chat/:id").handler(this::getChatById);
        //assign server
        server.requestHandler(router).listen(4567, res->{
            if (res.succeeded()){
                System.out.println("Created a server on port 4567");
            } else {
                System.out.println("Unable to create server: \n"+res.cause().getLocalizedMessage());
                fut.fail(res.cause());
            }
        });
        return fut;
    }

    private Future<Void> initAuthProvider(){
        Future<Void> fut  = Future.future();
        try{
            this.authProvider = new AuthProvider();
            fut.complete();
        } catch (Exception ex){
            System.out.println("Unable to create authprovider:\n"+ex.getLocalizedMessage());
            fut.fail(ex);
        }
        return fut;
    }

    private void getToken(RoutingContext ctx){
        ctx.response().setStatusCode(200).end(authProvider.generateToken(new JsonObject(), new JWTOptions()));
    }

    private void getChatById(RoutingContext ctx){

        //Create a request
        String chatId = ctx.pathParam("id");
        JsonObject request = new JsonObject();
        request.put("chatId", chatId);

        //send request to consumer
        bus.send("consumer.service.messages.getchat", request, res->{
            if (res.succeeded()){
                ctx.response().setStatusCode(200).end(Json.encode(res.result().body()));
            } else {
                System.out.println("Unable to get a chat:\n"+res.cause().getLocalizedMessage());
                ctx.response().setStatusCode(404).end();
            }
        });
    }
}
