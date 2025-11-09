import { ArrowDownIcon } from "@phosphor-icons/react";

import { Button } from "../components/button";

export const CompletePage = ({ onComplete }: { onComplete: () => void }) => {
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
              Thank you for confirming, your account is on review from real
              person 🕰️
            </div>

            <img
              src="https://res.cloudinary.com/khaya-zulu/image/upload/v1762657852/ChatGPT_Image_Nov_9_2025_05_10_13_AM_xxkjvi.png"
              className="mt-2 rounded-xl h-32"
            />

            <Button
              onClick={onComplete}
              className="w-full mt-4 flex rounded-lg! justify-between items-center gap-2"
            >
              <b>Complete</b>
              <ArrowDownIcon />
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};
