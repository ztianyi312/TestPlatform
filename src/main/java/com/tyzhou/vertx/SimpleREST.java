/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package com.tyzhou.vertx;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import io.vertx.rxjava.ext.sql.SQLConnection;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import rx.Observable;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SimpleREST extends AbstractVerticle {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
      runJavaExample("TestPlatfoorm/src/main/java/", SimpleREST.class, false);
  }

  private Map<String, JsonObject> products = new HashMap<>();
  
  private JDBCClient client;

  @Override
  public void start() {

    setUpInitialData();

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.get("/products/:productID").handler(this::handleGetProduct);
    router.put("/products/:productID").handler(this::handleAddProduct);
    router.get("/products").handler(this::handleListProducts);
    
    client = JDBCClient.createShared(vertx, new JsonObject()
    .put("url", "jdbc:hsqldb:mem:test?shutdown=true")
    .put("driver_class", "org.hsqldb.jdbcDriver")
    .put("max_pool_size", 30));

    client.getConnectionObservable().subscribe(
            conn->{
             // Now chain some statements using flatmap composition
                Observable<JsonObject> resa = conn.updateObservable("create table test(id int primary key, name varchar(255))").
                    flatMap(result -> conn.updateObservable("insert into test values(1, 'Hello')"), 2).
                    flatMap(result -> conn.updateObservable("insert into test values(2, 'Hello2')"),2).
                    flatMap(result -> conn.queryObservable("SELECT * FROM test").flatMap(resultSet->Observable.from(resultSet.getRows()),2),2);
                
                

                // Subscribe to the final result
                resa.subscribe(obj -> {
                  System.out.println("Results : " + obj);
                });
                Observable<ResultSet> resb = conn.queryObservable("SELECT * FROM test");
                resb.subscribe(resultSet -> {
                    System.out.println("Results : " + resultSet.getRows());
                  });
            },
            err->{
                err.printStackTrace();
            }
            );


    //vertx.createHttpServer().requestHandler(router::accept).listen(8080);
  }

  private void handleGetProduct(RoutingContext routingContext) {
    String productID = routingContext.request().getParam("productID");
    HttpServerResponse response = routingContext.response();
    if (productID == null) {
      sendError(400, response);
    } else {
      JsonObject product = products.get(productID);
      if (product == null) {
        sendError(404, response);
      } else {
        response.putHeader("content-type", "application/json").end(product.encodePrettily());
      }
    }
  }

  private void handleAddProduct(RoutingContext routingContext) {
    String productID = routingContext.request().getParam("productID");
    HttpServerResponse response = routingContext.response();
    if (productID == null) {
      sendError(400, response);
    } else {
      JsonObject product = routingContext.getBodyAsJson();
      if (product == null) {
        sendError(400, response);
      } else {
        products.put(productID, product);
        response.end();
      }
    }
  }

  private void handleListProducts(RoutingContext routingContext) {
    JsonArray arr = new JsonArray();
    products.forEach((k, v) -> arr.add(v));
    routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
  }

  private void sendError(int statusCode, HttpServerResponse response) {
    response.setStatusCode(statusCode).end();
  }

  private void setUpInitialData() {
    addProduct(new JsonObject().put("id", "prod3568").put("name", "Egg Whisk").put("price", 3.99).put("weight", 150));
    addProduct(new JsonObject().put("id", "prod7340").put("name", "Tea Cosy").put("price", 5.99).put("weight", 100));
    addProduct(new JsonObject().put("id", "prod8643").put("name", "Spatula").put("price", 1.00).put("weight", 80));
  }

  private void addProduct(JsonObject product) {
    products.put(product.getString("id"), product);
  }
  
  public static void runJavaExample(String prefix, Class clazz, boolean clustered) {
      runJavaExample(prefix, clazz, new VertxOptions().setClustered(clustered));
    }

    public static void runJavaExample(String prefix, Class clazz, VertxOptions options) {
      String exampleDir = prefix + clazz.getPackage().getName().replace(".", "/");
      runExample(exampleDir, clazz.getName(), options);
    }
    
  public static void runExample(String exampleDir, String verticleID, boolean clustered) {
      runExample(exampleDir, verticleID, new VertxOptions().setClustered(clustered));
    }
  
  public static void runExample(String exampleDir, String verticleID, VertxOptions options) {
      runExample(exampleDir, verticleID, options, null);
    }
  
  public static void runExample(String exampleDir, String verticleID, VertxOptions options, DeploymentOptions deploymentOptions) {
      System.setProperty("vertx.cwd", exampleDir);
      Consumer<Vertx> runner = vertx -> {
        try {
          if (deploymentOptions != null) {
            vertx.deployVerticle(verticleID, deploymentOptions);
          } else {
            vertx.deployVerticle(verticleID);
          }
        } catch (Throwable t) {
          t.printStackTrace();
        }
      };
      if (options.isClustered()) {
        Vertx.clusteredVertx(options, res -> {
          if (res.succeeded()) {
            Vertx vertx = res.result();
            runner.accept(vertx);
          } else {
            res.cause().printStackTrace();
          }
        });
      } else {
        Vertx vertx = Vertx.vertx(options);
        runner.accept(vertx);
      }
    }
  
  private void execute(SQLConnection conn, String sql, Handler<Void> done) {
      conn.execute(sql, res -> {
        if (res.failed()) {
          throw new RuntimeException(res.cause());
        }

        done.handle(null);
      });
    }

    private void query(SQLConnection conn, String sql, Handler<ResultSet> done) {
      conn.query(sql, res -> {
        if (res.failed()) {
          throw new RuntimeException(res.cause());
        }

        done.handle(res.result());
      });
    }
    
}