document.getElementById("submitOrder").addEventListener("click", function () {
    const selectedProducts = Array.from(document.getElementById("products").selectedOptions);
    const totalValue = selectedProducts.reduce((sum, option) => {
        const price = parseFloat(option.textContent.split(" - R$ ")[1]);
        return sum + price;
    }, 0);

    if (confirm(`O valor total do pedido é R$ ${totalValue.toFixed(2)}. Deseja confirmar?`)) {
        document.getElementById("orderForm").submit();
    }
});
