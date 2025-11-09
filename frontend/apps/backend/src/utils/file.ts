import type z from "zod";

export const convertFileToUri = async ({ file }: { file: z.core.File }) => {
  const arrayBuffer = await file.arrayBuffer();
  const uint8Array = new Uint8Array(arrayBuffer);

  const charArray = Array.from(uint8Array, (byte) => String.fromCharCode(byte));
  const binaryString = charArray.join("");
  const base64Data = btoa(binaryString);
  const fileDataUrl = `data:application/pdf;base64,${base64Data}`;

  return { uri: fileDataUrl };
};
