# Digigoods API

Digigoods is the backend API for a web marketplace for purchasing digital goods.

## Features

The API provides the following core services:

- Users Service: Manages customer data and authentication. For this initial prototype, authentication is handled via a simple username and password.
- Products Service: Provides a catalogue of available digital goods, such as prepaid mobile credits, digital storefront points (e.g., Steam, Google Play), and subscription codes (e.g., Spotify, Netflix).
- Shopping Cart Service: Manages the list of products a user intends to purchase. It interfaces with the Discount Service to calculate the final price.
- Discount Service: Manages all coupon codes and discount logic. It computes applicable discounts based on the contents of a shopping cart and the coupon codes applied.
- Order Service: Creates an order ticket from a user's shopping cart after checkout. It also tracks the order's status through to manual fulfillment.

## AI Assistance Disclosure

To maintain transparency and provide acknowledgements, we disclose the use of AI in this project.
[Gemini 2.5 Pro API](https://cloud.google.com/vertex-ai/generative-ai/docs/models/gemini/2-5-pro) was used to help brainstorm and refine the requirements, while [Augment Code](https://www.augmentcode.com/) served as an AI chat assistant and agent during development of the project.

## License

This project is licensed under the [MIT License](./LICENSE.md).