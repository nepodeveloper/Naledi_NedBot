export const createReadAsDataURL = (file: File) => {
  const reader = new FileReader();
  reader.readAsDataURL(file);

  return new Promise<string | undefined>((resolve) => {
    reader.addEventListener(
      "load",
      () => {
        resolve(reader.result?.toString());
      },
      false
    );
  });
};
