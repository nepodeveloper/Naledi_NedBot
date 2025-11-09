import { cn } from "../utils/cn";
import ReactMarkdown from "react-markdown";

export const Markdown = ({
  children,
  className,
}: {
  children: string | null | undefined;
  className?: string;
}) => {
  return (
    <div className={cn("wrap-break-word", className)}>
      <ReactMarkdown
        components={{
          h2: (props) => (
            <b
              {...props}
              className="font-semibold mt-6 mb-4 first:mt-0 last:mb-0"
            />
          ),
          p: (props) => (
            <p
              {...props}
              className="my-2.5 leading-relaxed first:mt-0 last:mb-0 whitespace-pre-wrap"
            />
          ),
          ul: (props) => (
            <ul
              {...props}
              className="list-outside list-disc ml-4 space-y-1 leading-normal"
            />
          ),
          ol: (props) => (
            <ol
              {...props}
              className="list-outside list-decimal ml-4 space-y-1 leading-normal"
            />
          ),
          li: (props) => (
            <li {...props} className="whitespace-normal pl-2 my-2.5" />
          ),
          strong: (props) => <strong {...props} className="font-semibold" />,
          em: (props) => <em {...props} className="italic" />,
          u: (props) => <u {...props} className="underline" />,
          a: (props) => (
            <a
              {...props}
              className="text-primary-500 hover:underline font-semibold"
            />
          ),
        }}
      >
        {children}
      </ReactMarkdown>
    </div>
  );
};
