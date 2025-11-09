export const callBankingApi = async (input: {
  body: any;
  endpoint: `/${string}`;
}) => {
  const result = await fetch(
    `https://nedbot-app.azurewebsites.net${input.endpoint}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  if (result.ok) {
    throw new Error("");
  }

  await result.json();

  return {
    businessName: "Nike",
  };
};
