function copyCampaignLink(button) {
    const url = button.getAttribute('data-url');
    if (!url) {
        return;
    }
    navigator.clipboard.writeText(url).then(() => {
        const label = button.textContent;
        button.textContent = 'Скопійовано!';
        setTimeout(() => {
            button.textContent = label;
        }, 2000);
    }).catch(() => {
        window.prompt('Скопіюйте посилання:', url);
    });
}

function addExpenseLine() {
    const container = document.getElementById('expense-lines');
    if (!container) {
        return;
    }
    const rows = container.querySelectorAll('.expense-line-row');
    const template = rows[0];
    const clone = template.cloneNode(true);
    clone.querySelectorAll('input[type="number"]').forEach(i => {
        i.value = '';
    });
    clone.querySelectorAll('input[type="text"]').forEach(i => {
        i.value = '';
    });
    container.appendChild(clone);
}
