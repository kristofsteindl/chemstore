import { Fragment } from "react"
import { createPortal } from "react-dom"
import classes from "./Modal.module.css"

const Backdrop = props => {
    return (
        <div onClick={props.onClick} className={classes.backdrop}></div>
    )
}

const ModalOverlay = props => {
    return (
        <div className={classes.modal}>
            <div className={classes.content}>
                {props.children}
            </div>
        </div>
    )
}

const portalElement = document.getElementById("overlays")

const Modal = props => {
    
    return (
        
        <Fragment>
            {createPortal(<Backdrop onClick={props.onClick}/>, portalElement)}
            {createPortal( <ModalOverlay>{props.children}</ModalOverlay>, portalElement)}
        </Fragment>
    )
}

export default Modal