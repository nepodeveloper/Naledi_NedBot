import { cn } from "../utils/cn";

export const LoadingIndicator = (props: { className?: string }) => {
  return (
    <div className="inline-flex gap-2">
      <div
        className={cn(
          "animate-bounce size-2 bg-primary-500 rounded-full",
          props.className
        )}
        style={{ animationDuration: "1.5s" }}
      />
      <div
        className={cn(
          "animate-bounce size-2 bg-primary-700 rounded-full",
          props.className
        )}
        style={{ animationDelay: "0.1s", animationDuration: "1.5s" }}
      />
      <div
        className={cn(
          "animate-bounce size-2 bg-primary-900 rounded-full",
          props.className
        )}
        style={{ animationDelay: "0.2s", animationDuration: "1.5s" }}
      />
    </div>
  );
};
