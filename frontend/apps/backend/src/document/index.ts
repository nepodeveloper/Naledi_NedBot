import { serverRoute } from "../utils/route.js";

import { uploadDocumentRoute, UploadDocumentInputSchema } from "./upload.js";
import {
  getBusinessInformationRoute,
  GetBusinessInformationInputRoute,
} from "./business.js";

export const document = {
  upload: serverRoute
    .input(UploadDocumentInputSchema)
    .handler(uploadDocumentRoute),
  business: serverRoute
    .input(GetBusinessInformationInputRoute)
    .handler(getBusinessInformationRoute),
};
