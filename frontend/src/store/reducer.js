import { intitialState } from "./index";
import { FORGOT_PASSWORD, NOTIFICATION, SIGN_IN, SIGN_OUT } from "./actionTypes";


export default function rootReducer(state = intitialState, action) {
    switch(action.type) {
        case SIGN_IN:
            localStorage.setItem('JWT', action.payload.jwt)
            return {
                ...state,
                jwt: action.payload.jwt
            }
        case SIGN_OUT:
            localStorage.removeItem('JWT')
            return {
                ...state,
                jwt: null
            }
        case NOTIFICATION:
            return {
                ...state,
                notification: action.payload.notification
            }
        default:
            return state;
    }
}