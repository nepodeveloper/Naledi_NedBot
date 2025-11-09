export const callBusinessApi = async ({ body }: { body: any }) => {
  const result = await fetch(`http://102.37.16.157:8080/api/agentMessage`, {
    method: "POST",
    body,
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!result.ok) {
    throw new Error("");
  }

  const data = await result.json();
  return data as { assistant: string };
};
