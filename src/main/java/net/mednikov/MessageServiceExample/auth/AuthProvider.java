package net.mednikov.MessageServiceExample.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;

import java.util.UUID;

public class AuthProvider implements JWTAuth {

    /*
    This is just a dummy AuthProvider and is a boilerplate:)
    If you need to implement AuthProvider, please consider my article:
    http://www.mednikov.net/case-studies/how-i-reinvented-a-wheel-building-sms-auth-in-vertx-with-java/
    and code:
    https://github.com/yuri-mednikov/vertx-sms-auth-example
    */

    private IAuthDao authDao;

    public AuthProvider() throws Exception{

    }

    @Override
    public String generateToken(JsonObject jsonObject, JWTOptions jwtOptions) {
        return UUID.randomUUID().toString();
    }

    @Override
    public void authenticate(JsonObject payload, Handler<AsyncResult<User>> handler) {
        String token = payload.getString("jwt");
        if (token!=null){
            AuthenticatedUser user = new AuthenticatedUser();
            handler.handle(Future.succeededFuture(user));
        } else {
            handler.handle(Future.failedFuture("Access denied! No token!"));
        }

    }

}
