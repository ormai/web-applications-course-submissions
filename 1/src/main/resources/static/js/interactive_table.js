const rows = document.querySelector("main table.table tbody");
const totalPrice = document.querySelector("#totalPrice");

totalPrice.textContent = [...document.querySelectorAll(".row-price")]
    .map(e => Number(e.textContent))
    .reduce((a, b) => a + b, 0)
    .toString();

function addRow(row, id) {
    rows.innerHTML += `<tr>
        <th scope="row">${id + 1}</th>
        <td>${row.name}</td>
        <td>${row.description}</td>
        <td class="row-price">${row.price}</td>
        <td><button class="btn btn-danger" data-bs-target="#confirm-del-row" data-bs-toggle="modal" type="button">
                <i class="bi bi-trash"></i>
        </button></td>
    </tr>`;
    totalPrice.innerText = Number(totalPrice.innerText) + row.price;
}

// connetti il tasto per eliminare ogni riga al modal e all'azione di rimozione
document.querySelector("#confirm-del-row").addEventListener("shown.bs.modal", e => {
    const row = e.relatedTarget.closest("tr");
    const yesButton = document.querySelector("#confirm-del-row #yes-del-row");
    const destructiveAction = () => {
        totalPrice.innerText = `${Number(totalPrice.innerText) - Number(row.children[3].innerText)}`;
        row.remove();
        yesButton.removeEventListener("click", destructiveAction);
    }
    yesButton.addEventListener("click", destructiveAction);
});

document.querySelector("#add-row-form").addEventListener("submit", e => {
    e.preventDefault();
    const name = document.querySelector("#new-prod-name").value,
        description = document.querySelector("#new-prod-description").value,
        price = Number(document.querySelector("#new-prod-price").value);
    addRow({name, description, price}, Number(rows.lastElementChild.children[0].innerText));
    e.target.reset();
});