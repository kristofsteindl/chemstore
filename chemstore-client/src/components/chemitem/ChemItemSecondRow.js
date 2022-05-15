const ChemItemSecondRow = props => {
    const { chemItem, getCollapseProps } = props
    const chemical = chemItem.chemical
    return (
        <div {...getCollapseProps()}>
            <div className="row content" style={{padding: "10px"}}>
                <div className="col-2">
                    <i className="mx-auto">{chemical.exactName}</i>
                </div>
                <div className="col-sm-1" />
                    
                <div className="col-1">
                    <i className="mx-auto">by {chemItem.arrivedBy.fullName}</i>
                </div>
                <div className="col-sm-4" />
                <div className="col-sm-1" />
                <div className="col-sm-1" >
                    <i className="mx-auto">{chemItem.openedBy && `by ${chemItem.openedBy.fullName}`}</i>
                </div>
                <div className="col-sm-1" />
                <div className="col-sm-1" />
            </div>
        </div>
    )
}

export default ChemItemSecondRow