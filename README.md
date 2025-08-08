# Digigoods API

Digigoods is the backend API for a web marketplace for purchasing digital goods.

## Features

The main feature of this API is simulating the use case when customer purchasing digital goods with discounts.
A user authenticates first to obtain the JWT token, then use the token to call the checkout endpoint to create an order.

The other features present in the API are:

- Get list of products available in the marketplace
- Get list of discounts available in the marketplace



## AI Assistance Disclosure

[Gemini 2.5 Pro API](https://cloud.google.com/vertex-ai/generative-ai/docs/models/gemini/2-5-pro) was used to help brainstorm and refine the requirements, while [Augment Code](https://www.augmentcode.com/) served as an AI chat assistant and agent during development of the project. The requirements are documented in [`docs/PROMPT.md`](./docs/PROMPT.md).

## License

This project is licensed under the [MIT License](./LICENSE.md).
