import { intitialState } from "./index";
import { FORGOT_PASSWORD, MY_PROFILE, NOTIFICATION, REMOVE_MESSENGER, SIGN_IN, SIGN_OUT } from "./actionTypes";


export default function rootReducer(state = intitialState, action) {
    switch(action.type) {
        case SIGN_IN:
            localStorage.setItem('JWT', action.payload.jwt)
            return {
                ...state,
                jwt: action.payload.jwt,
                profile: null,
                notification: 'Signed In!'
            }
        case SIGN_OUT:
            localStorage.removeItem('JWT')
            return {
                ...state,
                jwt: null,
                notification: 'Signed Out!'
            }
        case NOTIFICATION:
            return {
                ...state,
                notification: action.payload.notification
            }
        case MY_PROFILE:
            return {
                ...state,
                profile: action.payload.profile
            }
        case REMOVE_MESSENGER:
            return {
                ...state,
                profile: {
                    ...state.profile,
                    facebook: null
                }
            }
        default:
            return state;
    }
}