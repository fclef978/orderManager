function orderAjax(flavor, target, callBack) {
    // 各フィールドから値を取得してJSONデータを作成
    var j = {
        "flavor": flavor
    };

    // 通信実行
    $.ajax({
        type: "POST",
        url: "http://localhost:8888/" + target,
        cache: false,
        scriptCharset: 'utf-8',
        contentType: 'application/json; charset=utf-8',
        dataType: "json",
        data: JSON.stringify(j),
        success: function(json_data) {
            callBack(json_data);
        }
    });
}
