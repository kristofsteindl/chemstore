import { combineReducers } from 'redux'
import errorReducer from './errorReducer'
import securityReducer from './securityReducer'
import selectedLabReducer from './selectedLabReducer'

export default combineReducers ({
    errors: errorReducer,
    security: securityReducer,
    selectedLab: selectedLabReducer
})