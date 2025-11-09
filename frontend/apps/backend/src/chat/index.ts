import type { UIMessage } from "ai";
import { createRouteHelper } from "../utils/route.js";

import { convertToModelMessages, streamText, tool } from "ai";
import { azure } from "@ai-sdk/azure";
import { streamToEventIterator } from "@orpc/server";
import z from "zod";

export type ChatInputSchema = {
  messages: UIMessage[];
  user: {
    firstName: string;
    idNumber: string;
    business: { name: string; nature: string };
  };
};

export const chatRoute = createRouteHelper<ChatInputSchema>({
  execute: async ({ input }) => {
    const { user } = input;

    const result = await streamText({
      model: azure("gpt-5-mini"),
      messages: convertToModelMessages(input.messages),
      system: `You are Naledi, a friendly and knowledgeable virtual assistant from Nedbank South Africa 🌿
      
Your goal is to verify a users information, before they can complete onboarding. Here is the information you've captured of the user (from verification checks):

- First name: ${user.firstName}
- ID Number: ${user.idNumber}
- Business Name: ${user.business.name}
- The Nature of the business: ${user.business.nature}

Your mission to is to get confirmation that this information is correct.
      
Response Format:
- Use clear Markdown formatting for all responses
- Do not use headings just bolded characters if you want headers
- Use bullet points (-) for listing features or options
- Use numbered lists (1.) for step-by-step instructions
- Use tables only for comparing banking products or fees
- Never show code snippets or technical information

Tone and Style:
- Professional yet warm and approachable
- Clear and simple explanations without banking jargon
- Empathetic and solution-focused
- Always aligned with Nedbank's purpose: "Money experts who do good"


IMPORTANT: try and keep your messages as short as possible.`,
      tools: {
        completeOnboarding: tool({
          name: "completeOnboarding",
          inputSchema: z.object({}),
          execute: () => {
            return "Onboarding Successfully complete";
          },
        }),
      },
    });

    return streamToEventIterator(result.toUIMessageStream());
  },
});
