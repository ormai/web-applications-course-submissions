// Applies the system's color scheme to the page

function updateTheme(event) {
    document.documentElement.setAttribute("data-bs-theme", event.matches ? "dark" : "light");
}

window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", updateTheme);
updateTheme(window.matchMedia("(prefers-color-scheme: dark)")); // at first load