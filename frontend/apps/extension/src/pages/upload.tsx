import { useState } from "react";

import { IdentificationCardIcon } from "@phosphor-icons/react";
import { useMutation } from "@tanstack/react-query";
import { orpc } from "../orpc/client";
import { FileUpload } from "../components/file-upload";
import { Button } from "../components/button";

import { type User } from "../utils/types";
import { LoadingIndicator } from "../components/loading-indicator";

export const UploadPage = ({ onDone }: { onDone: (result: User) => void }) => {
  const [uploadedFile, setUploadedFile] = useState<File>();

  const [isLoading, setIsLoading] = useState<
    "extracting" | "verifying" | false
  >(false);

  const uploadDocumentMutation = useMutation(
    orpc.document.upload.mutationOptions({})
  );

  const businessDocumentationMutation = useMutation(
    orpc.document.business.mutationOptions({})
  );

  const handleSubmit = async () => {
    if (!uploadedFile) return;

    setIsLoading("extracting");
    const uploadResult = await uploadDocumentMutation.mutateAsync(uploadedFile);

    setIsLoading("verifying");
    const business = await businessDocumentationMutation.mutateAsync({
      idNumber: uploadResult.identityNumber,
    });

    onDone({
      firstName: uploadResult.name,
      idNumber: uploadResult.identityNumber,
      business,
    });

    setIsLoading(false);
  };

  return (
    <>
      <div className="rounded-xl border-2 border-dashed! bg-zinc-50 border-zinc-200 flex-1 flex flex-col items-center justify-center relative cursor-pointer">
        {!isLoading ? (
          <>
            <IdentificationCardIcon className="text-secondary-800 size-8" />
            <div className="mt-2 text-center px-2">
              {uploadedFile ? uploadedFile.name : "Upload your ID"}
            </div>
          </>
        ) : null}

        {isLoading ? (
          <div className="text-center flex flex-col items-center">
            <LoadingIndicator className="scale-125 mb-3" />
            <div className="mt-2">
              {isLoading === "extracting" ? (
                <>
                  Getting your ID and Name <br />
                  for verification
                </>
              ) : (
                <>
                  Getting the nature <br /> of your business
                </>
              )}
            </div>
          </div>
        ) : null}

        <FileUpload
          disabled={!!isLoading}
          accept=".pdf,application/pdf"
          onFileChange={({ file }) => {
            setUploadedFile(file);
          }}
        />
      </div>
      <Button
        onClick={handleSubmit}
        isLoading={!!isLoading}
        isDisabled={!uploadedFile}
      >
        Upload
      </Button>
    </>
  );
};
