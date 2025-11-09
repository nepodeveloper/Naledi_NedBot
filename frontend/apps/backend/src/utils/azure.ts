import { createAzure } from "@ai-sdk/azure";

import "dotenv/config";

export const azure = createAzure({
  resourceName: process.env.AZURE_RESOURCE_NAME,
  apiKey: process.env.AZURE_API_KEY,
});
