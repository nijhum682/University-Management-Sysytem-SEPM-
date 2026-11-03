// University Management System - REST API Helpers

async function submitEntityForm(event, formId, endpoint, isEdit = false, id = null) {
    event.preventDefault();
    const form = document.getElementById(formId);
    const formData = new FormData(form);
    const payload = {};

    formData.forEach((value, key) => {
        if (value.trim() !== '') {
            payload[key] = value.trim();
        }
    });

    const url = isEdit ? `${endpoint}/${id}` : endpoint;
    const method = isEdit ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            window.location.reload();
        } else {
            const err = await response.json();
            let msg = err.message || 'Operation failed';
            if (err.details && err.details.length > 0) {
                msg += ':\n- ' + err.details.join('\n- ');
            }
            alert(msg);
        }
    } catch (e) {
        alert('An unexpected network error occurred: ' + e.message);
    }
}

async function deleteEntity(endpoint, id, entityName) {
    if (!confirm(`Are you sure you want to delete this ${entityName}?`)) {
        return;
    }

    try {
        const response = await fetch(`${endpoint}/${id}`, {
            method: 'DELETE'
        });

        if (response.ok || response.status === 204) {
            window.location.reload();
        } else {
            const err = await response.json();
            alert(err.message || 'Failed to delete record');
        }
    } catch (e) {
        alert('An error occurred while deleting: ' + e.message);
    }
}
