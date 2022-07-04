var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

var _tags = [];
var _files = [];

window.addEventListener('load', (e) => {
    tags.forEach(element => {
        _tags.push(element.tag);
    });

    renderTags();
});

var renderTags = () => {
    let view = '';

    _tags.forEach(element => {
        view += `<li class="list-inline-item mt-1">
                    <button type="button" class="btn btn-light" onclick="clickDeleteTag(${element.id})">
                        ${element.name} <i class="bi bi-x-circle text-muted"></i>
                    </button>
                </li>`;
    });

    view += `<li class="list-inline-item mt-1">
                <button type="button" class="btn btn-link" data-bs-toggle="modal" data-bs-target="#exampleModal"><i class="bi bi-plus-circle text-muted"></i></button>
            </li>`;

    $('#render-tags').replaceWith(`<ul class="list-inline" id="render-tags">${view}</ul>`);
}

var clickAddTag = () => {
    const tagName = exampleModalInput.value;

    if (tagName != '' && _tags.filter(e => e.name === tagName).length == 0) {

        let boardTagForm = {
            tagName: tagName
        }

        $.ajax({
            type: 'POST',
            url: `/tags/api/add`,
            contentType: 'application/json',
            // dataType: 'json',
            data: JSON.stringify(boardTagForm),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (tag) {
                _tags.push(tag);
                renderTags();
            },
            error: function (error) {
                console.error(`Error: ${error}`);
            }
        });
    }
}

var clickDeleteTag = (tagId) => {

    if (tagId) {
        _tags = _tags.filter(e => e.id !== tagId);
        renderTags();
    }
}