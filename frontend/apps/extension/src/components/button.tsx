import { ComponentProps } from "react";
import { cn } from "../utils/cn";

type ButtonProps = ComponentProps<"button"> & {
  isDisabled?: boolean;
  isLoading?: boolean;
};

export const Button = ({ isDisabled, isLoading, ...props }: ButtonProps) => {
  const isBtnDisabled = props.disabled || isDisabled || isLoading;

  return (
    <button
      {...props}
      disabled={isBtnDisabled}
      className={cn(
        "bg-secondary-800 py-2.5 px-4 text-white rounded-xl! text-sm transition-colors font-bold",
        {
          "bg-secondary-200": isBtnDisabled,
        },
        props.className
      )}
    />
  );
};
