import axios from 'axios';
import { NOTIFICATION, SIGN_IN, SIGN_OUT } from './actionTypes';

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
        }).catch(err => dispatch(notifyUser(err.message)))
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
        }).catch(err => dispatch(notifyUser(err.message)))
    }
}

export const notifyUser = (notification) => ({
    type: NOTIFICATION,
    payload: {
        notification
    }
})

