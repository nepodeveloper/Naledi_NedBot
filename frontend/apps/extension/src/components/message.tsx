import { UIMessage } from "@ai-sdk/react";
import { cn } from "../utils/cn";
import Markdown from "react-markdown";

export const Message = ({ message }: { message: UIMessage }) => {
  const isAssistant = message.role === "assistant";

  const content = message.parts
    .map((part) => (part.type === "text" ? part.text : ""))
    .join("");

  return (
    <div className={cn("flex gap-2", isAssistant ? "pr-4" : "pl-4 ml-auto")}>
      <img
        src="https://res.cloudinary.com/khaya-zulu/image/upload/v1762633246/naledi_qaewkn.png"
        className={cn("h-8 w-8 object-cover rounded-full object-top", {
          "opacity-0": !isAssistant,
        })}
        aria-hidden={isAssistant}
      />
      <div>
        {isAssistant ? (
          <div className="text-sm font-semibold">Naledi (Nedbank)</div>
        ) : null}

        <div
          className={cn(
            !isAssistant && "border border-zinc-200 rounded-lg p-2 bg-zinc-50"
          )}
        >
          <Markdown>{content}</Markdown>
        </div>
      </div>
    </div>
  );
};
