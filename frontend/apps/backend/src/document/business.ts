import z from "zod";
import { createRouteHelper } from "../utils/route.js";
import { callBankingApi } from "../utils/banking.js";
import { callBusinessApi } from "../utils/business.js";
import { generateObject } from "ai";
import { azure } from "@ai-sdk/azure";

export const GetBusinessInformationInputRoute = z.object({
  idNumber: z.string(),
});

export const getBusinessInformationRoute = createRouteHelper({
  inputSchema: GetBusinessInformationInputRoute,
  execute: async ({ input }) => {
    const data = await callBankingApi({
      body: input,
      endpoint: "/api/business",
    });

    const business = await callBusinessApi({
      body: `Get me the business nature of ${data.businessName}`,
    });

    const result = await generateObject({
      model: azure("gpt-5-mini"),
      system: `Your job is to get the nature of business from the search of a business.`,
      prompt: business.assistant,
      schema: z.object({
        natureOfBusiness: z.string(),
      }),
    });

    return {
      name: data.businessName,
      nature: result.object.natureOfBusiness,
    };
  },
});
