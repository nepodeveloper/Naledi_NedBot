import { chatRoute, type ChatInputSchema } from "./chat/index.js";
import { document } from "./document/index.js";
import { serverRoute } from "./utils/route.js";

import { type } from "@orpc/server";

export const router = {
  document,
  chat: serverRoute.input(type<ChatInputSchema>()).handler(chatRoute),
};
