const { GoogleGenerativeAI } = require("@google/generative-ai");

const { GEMINI_TOKEN } = Bun.env;

const genAI = new GoogleGenerativeAI(GEMINI_TOKEN);
const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });

const server = Bun.serve({
    port: 3000,
    async fetch(request) {
        if (request.method === "POST") {
            const body = await Bun.readableStreamToText(request.body!);
            const result = await model.generateContent(body);

            return new Response(result.response.text());
        }

        return new Response("Hi!");
    },
});
  
console.log(`Listening on localhost:${server.port}`);