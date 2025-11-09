import { ArrowRightIcon } from "@phosphor-icons/react";

import { Button } from "../components/button";

export const IndexPage = ({ onBegin }: { onBegin: () => void }) => {
  return (
    <>
      <div className="text-sm">
        <div className="flex gap-2">
          <img
            src="https://res.cloudinary.com/khaya-zulu/image/upload/v1762633246/naledi_qaewkn.png"
            className="h-8 w-8 object-cover rounded-full object-top"
          />
          <div>
            <div className="text-sm font-semibold">Naledi (Nedbank)</div>
            <div>
              Hi there 🍃 I'm Naledi,
              <br />
              <br />
              your dedicated Nedbank business banking assistant.{" "}
              <b> To get started, I'll need to verify your ID.</b>
            </div>

            <img
              src="https://res.cloudinary.com/khaya-zulu/image/upload/v1762633246/id_copy_hzffqb.png"
              className="mt-2 rounded-xl h-32"
            />

            <Button
              onClick={() => onBegin()}
              className="w-full mt-4 flex rounded-lg! justify-between items-center gap-2"
            >
              <b>Let's begin</b>
              <ArrowRightIcon />
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};
