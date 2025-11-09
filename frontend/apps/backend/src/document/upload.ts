import { z } from "zod";

import { createRouteHelper } from "../utils/route.js";

import { azure } from "../utils/azure.js";

import { generateObject } from "ai";
import { convertFileToUri } from "../utils/file.js";

export const UploadDocumentInputSchema = z.file();

export const uploadDocumentRoute = createRouteHelper({
  inputSchema: UploadDocumentInputSchema,
  execute: async ({ input }) => {
    const fileResult = await convertFileToUri({ file: input });

    const result = await generateObject({
      model: azure.responses("gpt-5-mini"),
      schema: z.object({
        name: z.string(),
        identityNumber: z
          .string()
          .describe("The users government identity number"),
      }),
      messages: [
        {
          role: "user",
          content: [
            {
              type: "text",
              text: "Can you extract information for verification from my government id document.",
            },
            {
              type: "file",
              data: fileResult.uri,
              mediaType: "application/pdf",
            },
          ],
        },
      ],
    });

    console.log("🔥".repeat(10), result.object);

    return result.object;
  },
});
