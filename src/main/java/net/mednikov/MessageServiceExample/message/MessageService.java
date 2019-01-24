package net.mednikov.MessageServiceExample.message;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class MessageService extends AbstractVerticle {

    @Inject
    private IMessageDao messageDao;

    public MessageService(){
        Guice.createInjector(new MessageServiceModule()).injectMembers(this);
    }

    @Override
    public void start() throws Exception {
        super.start();

        //get a reference to EventBus instance
        EventBus bus = vertx.eventBus();

        //assign a consumer
        bus.consumer("consumer.service.messages.getchat").handler(msg->{

            //get a payload from message
            JsonObject request = JsonObject.mapFrom(msg.body());
            /*just to be sure, output a result for debug purposes:*/
            System.out.println("MessageService received from EventBus: \n"+request.toString());

            //perform DAO operation to fetch data from datasource
            Chat result = messageDao.findChatById(request.getString("chatId"));

            //reply back to sender
            msg.reply(JsonObject.mapFrom(result));
        });
    }
}
