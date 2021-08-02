import axios from 'axios';
import store from "./index";
import { NOTIFICATION, SIGN_IN, SIGN_OUT, UNAUTHORIZED, MY_PROFILE, REMOVE_MESSENGER } from './actionTypes';

const API_URL = 'http://localhost:8080'
const MAPBOX_GEOCODING_API = 'https://api.mapbox.com/geocoding/v5/mapbox.places'
const MAPBOX_ACCESS_TOKEN = 'pk.eyJ1IjoibWprc2FiaXQiLCJhIjoiY2tvc3hlZXFxMDV6MjJwcWo3ejF0dHp5NSJ9.X19XUxgb1BCjxScWSUsg1g'
const IMGBB_API = "https://api.imgbb.com/1/upload"
const IMGBB_API_KEY = 'f96dcb0d10309fcb8be71d9b7a82022a'

export const mapboxGeoCoding = async (searchText) => {
    const {data} = await axios.get(
        `${MAPBOX_GEOCODING_API}/${encodeURIComponent(searchText)}.json?access_token=${MAPBOX_ACCESS_TOKEN}&limit=10`
    )
    const places = []
    data.features.forEach( value => {
        places.push({
            name: value.place_name,
            long: value.center[0],
            lat: value.center[1]
        })
    })
    return places
}

export const uploadImage = async (file) => {
    const formData = new FormData()
    formData.append("image", file);
    const {data} = await axios.post(`${IMGBB_API}?key=${IMGBB_API_KEY}`,
        formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
    
    return data.data.url
}

export const signIn = (username, password) => {
    return (dispatch, state) => {
        axios.post(API_URL+'/authenticate', {username, password}).then( response => {
            dispatch({
                type: SIGN_IN,
                payload: {
                    jwt: response.data.jwt
                }
            })
        }).catch(e => console.log(e))
    }
}

export const signOut = () => {
    return {
        type: SIGN_OUT
    }
}

export const forgotPassword = (username) => {
    return (dispatch, state) => {
        axios.post(`${API_URL}/forgot`, {username}).then(response => {
            dispatch(notifyUser('Check your Email for reset link!'))
        }).catch(e => console.log(e))
    }
}

export const notifyUser = (notification) => ({
    type: NOTIFICATION,
    payload: {
        notification
    }
})


export const signUpUser = async (user) => {
    return await axios.post(`${API_URL}/register/user`, user)
}

export const createNewPost = async (data, notify=true) => {
    const response = await axios.post(`${API_URL}/user/post?notify=${notify}`, data)
    store.dispatch(notifyUser('Posted Sucessfully!'))
    return response.data
}

export const saveSettings = async (data) => {
    const response = await axios.post(`${API_URL}/user/change-profile`, data)
    store.dispatch(notifyUser('Saved Settings Sucessfully!'))
    store.dispatch({
        type: MY_PROFILE,
        payload: {
            profile: response.data
        }
    })
    return response
}

export const changePassword = async (oldPassword, newPassword) => {
    const data = {}
    data['old'] = oldPassword
    data['new'] = newPassword
    await axios.post(`${API_URL}/user/change-password`, data)
    store.dispatch(notifyUser('Password Changed Sucessfully!'))
}

export const getMessengerToken = async () => {
    const data = await axios.get(`${API_URL}/user/messenger-token`)
    return data.data
}

export const deleteMessengerToken = async () => {
    await axios.delete(`${API_URL}/user/messenger-token`)
    store.dispatch({
        type: REMOVE_MESSENGER
    })
}

export const getMyProfile = () => {
    return (dispatch, state) => {
        axios.get(`${API_URL}/user/profile`).then ( profile => {
            dispatch({
                type: MY_PROFILE,
                payload: {
                    profile: profile.data
                }
            })
        }).catch(e => console.log(e))
    }
}

export const getUserProfile = async (username) => {
    const response = await axios.get(`${API_URL}/user/profile/${username}`)
    return response.data
}

export const getPostDetails = async (id) => {
    const response = await axios.get(`${API_URL}/user/post/${id}`)
    return response.data
}

export const changePostManaged = async (id) => {
    const response = await axios.post(`${API_URL}/user/post/${id}/managed`)
    return response.data
}

export const deletePost = async (id) => {
    const response = await axios.delete(`${API_URL}/user/post/${id}`)
    return response.data
}

export const getPosts = async (url, page=0) => {
    const adder = url.match(/\?/) !== null ? '&' : '?'
    const data = await axios.get(`${API_URL}${url}${adder}page=${page}`)
    return {content: data.data.content, hasNext: !data.data.last, page: data.data.number}
}

axios.interceptors.response.use(
    response => response,
    error => {
        console.log(error)
      const status = error.response.status;

      if (status === UNAUTHORIZED) {
        store.dispatch(signOut());
      } else {
        store.dispatch(notifyUser(error.message))
        console.log(error)
        return Promise.reject(error);
      }
   }
);

axios.interceptors.request.use( config => {
    const jwt = store.getState().jwt
    if (jwt)
        config.headers.Authorization = `Bearer ${jwt}`
    return config
})