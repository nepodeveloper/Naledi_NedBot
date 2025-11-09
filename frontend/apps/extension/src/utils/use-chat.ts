import { orpc } from "../orpc/client";
import { eventIteratorToStream } from "@orpc/client";
import { useChat as useAIChat } from "@ai-sdk/react";

import { User } from "./types";

export const useChat = ({
  user,
  onOnboardingComplete,
}: {
  user: User;
  onOnboardingComplete: () => void;
}) => {
  return useAIChat({
    onToolCall: (tool) => {
      const toolName = tool.toolCall.toolName;
      if (toolName === "completeOnboarding") {
        onOnboardingComplete();
      }
    },
    transport: {
      async sendMessages(options) {
        return eventIteratorToStream(
          // @ts-expect-error
          await orpc.chat.call(
            {
              messages: options.messages,
              user,
            },
            { signal: options.abortSignal }
          )
        );
      },
      reconnectToStream() {
        throw new Error("Not implemented");
      },
    },
  });
};
