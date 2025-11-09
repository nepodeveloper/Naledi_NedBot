import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { ReactNode, useState } from "react";

import { IndexPage } from "./pages";
import { ChatPage } from "./pages/chat";
import { UploadPage } from "./pages/upload";
import { User } from "./utils/types";
import { cn } from "./utils/cn";
import { CompletePage } from "./pages/complete";

const Container = ({
  children,
  isOpen,
  onOpen,
}: {
  children: ReactNode;
  isOpen?: boolean;
  onOpen: () => void;
}) => {
  return (
    <div
      className={cn(
        "flex sticky bottom-0 left-0 p-10 w-full h-full z-100000 flex-col justify-end",
        { "p-4 inline-flex cursor-pointer": !isOpen }
      )}
    >
      <div
        className={cn(
          "rounded-3xl p-6 flex relative",
          isOpen
            ? "via-primary-900 bg-radial-[at_50%_75%] from-primary-700 to-primary-500 flex-1"
            : "bg-primary-700 p-2 w-46 rounded-full rounded-bl-none cursor-pointer"
        )}
        onClick={() => {
          if (!isOpen) onOpen();
        }}
      >
        {isOpen ? (
          <div className="rounded-xl bg-primary-900 flex-1 translate-y-1">
            <div className="bg-white w-full h-full -translate-y-0.5 rounded-xl flex flex-col gap-4 p-4">
              {children}
            </div>
          </div>
        ) : (
          <>
            <div className="flex gap-2.5 items-center">
              <img
                src="https://res.cloudinary.com/khaya-zulu/image/upload/v1762633246/naledi_qaewkn.png"
                alt=""
                className="h-10 w-10 rounded-full"
              />
              <b className="text-white">Talk to Naledi</b>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

function App() {
  const [queryClient] = useState(() => new QueryClient());

  const [isOpen, setIsOpen] = useState(false);

  const [page, setPage] = useState<"index" | "upload" | "chat" | "complete">(
    "index"
  );
  const [user, setUser] = useState<User>();

  return (
    <QueryClientProvider client={queryClient}>
      <Container isOpen={isOpen} onOpen={() => setIsOpen(true)}>
        {page === "index" ? (
          <IndexPage onBegin={() => setPage("upload")} />
        ) : null}
        {page === "upload" ? (
          <UploadPage
            onDone={(user) => {
              setUser(user);
              setPage("chat");
            }}
          />
        ) : null}
        {page === "chat" && user ? (
          <ChatPage
            user={user}
            onComplete={() => {
              setPage("complete");
            }}
          />
        ) : null}
        {page === "complete" ? (
          <CompletePage
            onComplete={() => {
              setIsOpen(false);
            }}
          />
        ) : null}
      </Container>
    </QueryClientProvider>
  );
}

export default App;
