import React from 'react'
import { Link } from 'react-router-dom'

function RedirectFormButton(props) {
    const toUrl = props.objectToPass ? props.objectToPass.formRoute : props.formRoute
    return (
        <Link to={{pathname: toUrl, state: props.objectToPass}} className="btn btn-lg btn-info">
            {props.buttonLabel}
        </Link>
    )
}

export default RedirectFormButton
