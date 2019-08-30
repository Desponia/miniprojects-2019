$(document).ready(function () {
    friendBarModule.init()
})

const friendBarModule = (function () {
    async function loadAllUser() {
        await api.GET("/api/users")
            .then(res => res.json())
            .then(data => {
                for (let i = 0; i < data.length; i++) {
                    createUserDOM(data[i])
                }
                addFriendsAskButtonClickListener()
            })
    }

    async function loadFriends() {
        await api.GET("/api/friends")
            .then(res => res.json())
            .then(data => {
                for (let i = 0; i < data.length; i++) {
                    createFriendDOM(data[i])
                }
                addFriendsRemoveBtnClickListener()
            })
    }

    function createUserDOM(user) {
        document.getElementById('all-user-list').insertAdjacentHTML('beforeend', allUserTemplate(user))
    }

    function createFriendDOM(friend) {
        document.getElementById('friends-list').insertAdjacentHTML('beforeend', friendTemplate(friend))
    }

    function addFriendsAskButtonClickListener() {
        const btns = document.getElementsByClassName('FriendAsk')
        for (let i = 0; i < btns.length; i++) {
            btns[i].addEventListener('click', async function (event) {
                const data = {"receiverId": event.target.parentNode.parentNode.getAttribute("data-id")}
                const res = await api.POST("/api/friends/asks", data)

	            if(res.status == 201) {
	                alert('친구 요청을 보냈습니다.')
	            } else if(res.status == 400) {
                    const error = await res.json()
    	            alert(error.message)
	            }
			})
		}
	}

	async function addFriendsRemoveBtnClickListener() {
		const btns = document.getElementsByClassName('FriendRemove')
		for(let i = 0; i < btns.length; i++) {
			btns[i].addEventListener('click', async function(event) {
                const friendId = event.target.parentNode.parentNode.getAttribute("data-id")
                const res = await api.DELETE("/api/friends/" + friendId)

	            if(res.status == 204) {
	                alert('친구가 삭제되었습니다.')
                    const grandParent = event.target.parentNode.parentNode
                    grandParent.parentNode.removeChild(grandParent)
	            } else if(res.status == 400) {
                    const error = await res.json()
    	            alert(error.message)
	            }
			})
		}
	}

    function preventDropdownHide() {
        $('div.btn-group.dropleft').on('click', function (event) {
            let events = $._data(document, 'events') || {}
            events = events.click || []
            for (let i = 0; i < events.length; i++) {
                if (events[i].selector) {
                    if ($(event.target).is(events[i].selector)) {
                        events[i].handler.call(event.target, event)
                    }

                    $(event.target).parents(events[i].selector).each(function () {
                        events[i].handler.call(this, event)
                    })
                }
            }
            event.stopPropagation()
        })
    }

    return {
        init: async function () {
            await loadAllUser()
            await loadFriends()
            preventDropdownHide()
        }
    }
})()

const friendBar = document.querySelector('.fbFriendSidebar')
const friendBarToggleBtn = document.getElementById('friend-bar-btn')
const FRIEND_BAR_SHOW_CLASS = 'show-friend-bar'
const FRIEND_BAR_HIDE_CLASS = 'hide-friend-bar'

friendBarToggleBtn.addEventListener('click', (event) => {
    const friendBarClasses = friendBar.classList

    if (friendBarClasses.contains(FRIEND_BAR_SHOW_CLASS)) {
        friendBar.classList.add(FRIEND_BAR_HIDE_CLASS)
        friendBar.classList.remove(FRIEND_BAR_SHOW_CLASS)
    } else {
        friendBar.classList.add(FRIEND_BAR_SHOW_CLASS)
        friendBar.classList.remove(FRIEND_BAR_HIDE_CLASS)
    }
})

window.addEventListener('resize', (event) => {
    if (innerWidth < 1430 && friendBar.classList.contains(FRIEND_BAR_SHOW_CLASS)) {
        friendBar.classList.add(FRIEND_BAR_HIDE_CLASS)
        friendBar.classList.remove(FRIEND_BAR_SHOW_CLASS)
    }

    if (innerWidth > 1430 && friendBar.classList.contains(FRIEND_BAR_HIDE_CLASS)) {
        friendBar.classList.add(FRIEND_BAR_SHOW_CLASS)
        friendBar.classList.remove(FRIEND_BAR_HIDE_CLASS)
    }
})