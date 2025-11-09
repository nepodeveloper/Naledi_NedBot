import { PaperPlaneRightIcon } from "@phosphor-icons/react";
import { Message } from "../components/message";
import { useChat } from "../utils/use-chat";
import { useEffect, useRef, useState } from "react";
import { User } from "../utils/types";
import { LoadingIndicator } from "../components/loading-indicator";

export const ChatPage = ({
  user,
  onComplete,
}: {
  user: User;
  onComplete: () => void;
}) => {
  const [val, setVal] = useState("");

  const bottomScrollRef = useRef<HTMLDivElement>(null);
  const submitRef = useRef<HTMLButtonElement>(null);
  const isMessageSentRef = useRef(false);

  const { messages, sendMessage, status } = useChat({
    user,
    onOnboardingComplete: () => {
      onComplete();
    },
  });

  const isSubmitted = status === "submitted";
  const isReady = status === "ready";

  const onKeyDown = (ev: React.KeyboardEvent<HTMLInputElement>) => {
    if (ev.key === "Enter" && !ev.shiftKey) {
      ev.preventDefault();
      submitRef.current?.click();
    }
  };

  const scrollContainerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const el = scrollContainerRef.current;
    if (!el) return;
    el.scrollTo({ top: el.scrollHeight, behavior: "smooth" });
  }, [messages.length, isReady]);

  useEffect(() => {
    if (isMessageSentRef.current) return;

    isMessageSentRef.current = true;
    sendMessage({
      text: "",
    });
  }, []);

  return (
    <>
      <div
        ref={scrollContainerRef}
        className="text-sm h-84 overflow-y-auto no-scrollbar"
      >
        <div className="flex flex-col gap-3">
          {messages.slice(1).map((m) => (
            <Message key={m.id} message={m} />
          ))}
          {isSubmitted ? <LoadingIndicator className="mt-2" /> : null}
          <div ref={bottomScrollRef} className="h-10" />
        </div>
      </div>
      <form
        className="border-t border-zinc-200 pt-3 px-0.5 flex gap-2 items-center"
        onSubmit={(ev) => {
          ev.preventDefault();
          console.log("messages", val);
          sendMessage({ text: val });
          setVal("");
        }}
      >
        <input
          type="text"
          placeholder="Type your message"
          onChange={(ev) => setVal(ev.target.value)}
          value={val}
          className="w-full outline-none"
          onKeyDown={onKeyDown}
        />

        <button ref={submitRef} type="submit">
          <PaperPlaneRightIcon className="size-5" />
        </button>
      </form>
    </>
  );
};
