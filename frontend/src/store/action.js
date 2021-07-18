import axios from 'axios';
import { NOTIFICATION, SIGN_IN, SIGN_OUT } from './actionTypes';

const API_URL = 'http://localhost:8080'

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

