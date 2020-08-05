/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kogito.serverless.demo;

import com.fasterxml.jackson.databind.JsonNode;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/finaldecisions/events")
@ApplicationScoped
public class ApprovalDecisionWebsocket {
    @Inject
    @Channel("approvaldecision")
    Flowable<String> approvalDecisionEvents;
    //Publisher<JsonNode> approvalDecisionEvents;

    private static final Logger LOGGER = Logger.getLogger(UserTaskEventsWebsocket.class);

    private List<Session> sessions = new CopyOnWriteArrayList<>();
    private Disposable subscription;

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @PreDestroy
    public void cleanup() {
        subscription.dispose();
    }

    @PostConstruct
    public void subscribe() {
        subscription = approvalDecisionEvents.subscribe(event -> sessions.forEach(session -> write(session, event)));
    }

    private void write(Session session, String event) {
        session.getAsyncRemote().sendText(event, result -> {
            if (result.getException() != null) {
                LOGGER.error("Unable to write workflow event to web socket", result.getException());
            }
        });
    }
}
