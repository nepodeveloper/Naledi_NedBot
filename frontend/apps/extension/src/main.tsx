import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";

const rootElement = document.getElementById("root")!;

rootElement.style.position = "sticky";
rootElement.style.bottom = "0px";
rootElement.style.left = "0px";
rootElement.style.width = "500px";
rootElement.style.height = "550px";
rootElement.style.border = "none";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
