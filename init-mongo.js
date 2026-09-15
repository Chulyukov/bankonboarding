db.createCollection('products');
db.createCollection('discounts');

db.products.insertMany([
    { _id: 1, name: "Bread", price: 70, inStock: true },
    { _id: 2, name: "Milk", price: 120, inStock: true },
    { _id: 3, name: "Sausage", price: 290, inStock: false }
]);

db.discounts.insertMany([
    {_id: 1, name: "New Year 2026", percentageSale: 20, isDiscountAvailable: true, createdAt: new Date(), products: [1, 3]},
    {_id: 2, name: "Milk Sale", percentageSale: 30, isDiscountAvailable: true, createdAt: new Date(), products: [2]},
    {_id: 3, name: "Sausage Sale", percentageSale: 50, isDiscountAvailable: true, createdAt: new Date(), products: [3]},
    {_id: 4, name: "Expired Sale", percentageSale: 70, isDiscountAvailable: false, createdAt: new Date(), products: [3]},
    {_id: 5, name: "Sale without products", percentageSale: 70, isDiscountAvailable: true, createdAt: new Date(), products: []}
]);