import { createReadAsDataURL } from "../utils/file";
import { type ComponentProps } from "react";

export const FileUpload = ({
  accept = "image/png, image/jpeg",
  className,
  onFileChange,
  ...props
}: {
  accept?: string;
  className?: string;
  onFileChange: (inputs: {
    file: File | undefined;
    readAsDataURL?: () => Promise<string | undefined>;
  }) => void;
} & ComponentProps<"input">) => {
  return (
    <input
      {...props}
      type="file"
      className={`h-full w-full absolute top-0 left-0 opacity-0 ${className}`}
      accept={accept}
      onChange={async (ev) => {
        const file = ev.target.files?.[0];
        const promise = file ? () => createReadAsDataURL(file) : undefined;

        onFileChange({ file, readAsDataURL: promise });
      }}
    />
  );
};
