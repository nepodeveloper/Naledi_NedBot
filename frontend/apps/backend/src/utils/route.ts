import { z } from "zod";
import { onError, os } from "@orpc/server";

type Input = z.ZodType | Record<string, any>;

export const createRouteHelper = <I extends Input, O extends any = unknown>({
  execute,
}: {
  inputSchema?: I;
  execute: (data: {
    input: I extends z.ZodType ? z.infer<I> : I;
  }) => Promise<O> | O;
}) => {
  return (data: { input: I extends z.ZodType ? z.infer<I> : I }) =>
    execute(data);
};

export const defaultMiddleware = os.middleware(async ({ next }) => {
  return next();
});

export const serverRoute = os.use(defaultMiddleware).use(
  onError((err) => {
    console.error("Route error:", err);
  })
);
