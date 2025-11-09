import { createORPCClient, onError } from "@orpc/client";
import { RPCLink } from "@orpc/client/fetch";
import { createTanstackQueryUtils } from "@orpc/tanstack-query";

import type { RouterClient } from "@orpc/server";
import type { InferRouterOutputs } from "@orpc/server";

import { router } from "@nedbank/backend";

export type RouterOutputs = InferRouterOutputs<typeof router>;

const link = new RPCLink({
  url: `http://localhost:3000/rpc`,
  headers: () => ({}),
  interceptors: [
    onError((error) => {
      console.error(error);
    }),
  ],
});

const client: RouterClient<typeof router> = createORPCClient(link);

export const orpc = createTanstackQueryUtils(client);
