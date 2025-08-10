// ajax
async function $ajax( obj ) {
    console.log('data:', obj.data);
    console.log('url:', obj.url);
    console.log('type:', obj.type);
    
    try {
        // 💎 CSRF TOKEN
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        let data
        if(obj.type == 'GET' || obj.type == 'DELETE') { data = obj.data; }
        else { data = obj.data instanceof FormData ? obj.data : JSON.stringify(obj.data)}
        let response = await $.ajax({
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            url: obj.url,
            type: obj.type,
            data: data,
            contentType: obj.data instanceof FormData ? false : 'application/json; charset=utf-8',
            processData: obj.data instanceof FormData ? false : true,                                    // 데이터를 쿼리 문자열로 변환하지 않음
            // dataType: 'json'              // 서버의 응답 데이터 타입 (단순 SUCCESS, FAIL 문자열일 경우 생략)
        });
        return response;
    } catch (error) {
        console.log('[ajax.js] error -------------------------');
        
        // console.error('Error:', error);
        return "FAIL"
    }   

}