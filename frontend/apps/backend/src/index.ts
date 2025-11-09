import { serve } from "@hono/node-server";
import { Hono } from "hono";
import { RPCHandler } from "@orpc/server/fetch";

import { router } from "./router.js";
import { onError } from "@orpc/server";
import { cors } from "hono/cors";

export { router };

const app = new Hono();

const handler = new RPCHandler(router, {
  interceptors: [
    onError((err) => {
      console.error("RPC Error:", err);
    }),
  ],
});

app.use("/rpc/*", cors());

app.use("/rpc/*", async (c, next) => {
  const { response, matched } = await handler.handle(c.req.raw, {
    prefix: "/rpc",
    context: {},
  });

  if (matched) {
    return c.newResponse(response.body, response);
  }

  await next();
});

app.get("/", (c) => {
  return c.text("Hello Hono!");
});

serve(
  {
    fetch: app.fetch,
    port: 3000,
  },
  (info) => {
    console.log(`Server is running on http://localhost:${info.port}`);
  }
);
