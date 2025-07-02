document.addEventListener('DOMContentLoaded', function () {
    if (checkAuthentication()) {
        loadUserInfo();
    }
})

function loadUserInfo() {
    fetch('/api/admin/info')
        .then(response => {
            if (!response.ok) {
                throw new Error("获取用户名称失败");
            }
            return response.json()
        })
        .then(data => {
            if (data.code === '0') {
                let headerUsername = document.getElementById('username-header');
                let headerUsername2 = document.getElementById('username-header2');
                let sidebarUsername = document.getElementById('username-sidebar');
                console.log(sidebarUsername)
                headerUsername2.textContent = data.data;
                headerUsername.textContent = data.data;
                sidebarUsername.textContent = data.data;
            } else {
                console.error("获取用户名失败: ", data.message);
            }
        })
        .catch(error => {
            console.error("请求失败", error)
        })
}