import React, { Component } from 'react'
import ChemItem from './ChemItem'
import RedirectFormButton from '../RedirectFormButton'
import { check } from '../../utils/securityUtils'
import axios from 'axios'
import { connect } from 'react-redux'
import Pagination from '../Pagination'

class ChemItemDashboard extends Component {

    constructor() {
        super()
        this.state = {
            chemItems: [],
            currentPage: 1, 
            totalItems: 0,
            totalPages: 0 
        }
    }

    componentDidMount() {
        const selectedLab = this.props.selectedLab
        this.loadChemItems(selectedLab)
    }

    async componentWillReceiveProps(nextProps){
        const selectedLab = nextProps.selectedLab
        this.loadChemItems(selectedLab)
    }
    
    loadChemItems(selectedLab) {
        check()
        if (selectedLab && selectedLab.key) {
            axios.get(`/api/chem-item/${selectedLab.key}?page=${0}&size=${3}`)
                .then(result => this.setState({
                    chemItems: result.data.content,
                    totalItems: result.data.totalItems,
                    totalPages: result.data.totalPages
                }))
                .catch(error => console.log(error))
        }
    }  
    
    onPageChanged = data => {
        check()
        const selectedLab = this.props.selectedLab
        if (selectedLab && selectedLab.key) {
            const { currentPage, totalPages, pageLimit } = data;
            console.log("on page changed " + currentPage)

            axios.get(`/api/chem-item/${selectedLab.key}?page=${currentPage - 1}&size=${pageLimit}`)
            .then(result => this.setState({
                chemItems: result.data.content,
                currentPage: currentPage,
                totalItems: result.data.totalItems,
                totalPages: result.data.totalPages
            }))
        }
      }

    render() {
        const { chemItems, totalItems, currentPage, totalPages } = this.state;
    
        if (totalItems === 0) return null;
    
        const headerClass = ['text-dark py-2 pr-4 m-0', currentPage ? 'border-gray border-right' : ''].join(' ').trim();

        return (
            <div className="projects">
                <div className="container">
                    <div className="row">
                        <div className="col-md-12">
                            <h3 className="display-4 text-center">Registered Chemicals</h3>
                            <RedirectFormButton formRoute="/add-chem-item" buttonLabel="Register Chemical"/>
                            <div className="container mb-5">
                                <div className="row d-flex flex-row py-5">
                                <div className="w-100 px-4 py-5 d-flex flex-row flex-wrap align-items-center justify-content-between">
                                    <div className="d-flex flex-row align-items-center">
                                    <h2 className={headerClass}>
                                        <strong className="text-secondary">{totalItems}</strong> Registered Chemicals
                                    </h2>
                                    { currentPage && (
                                        <span className="current-page d-inline-block h-100 pl-4 text-secondary">
                                        Page <span className="font-weight-bold">{ currentPage }</span> / <span className="font-weight-bold">{ totalPages }</span>
                                        </span>
                                    ) }
                                    </div>
                                    
                                    <div className="d-flex flex-row py-4 align-items-center">
                                        <Pagination 
                                            totalRecords={totalItems} 
                                            pageLimit={4} 
                                            pageNeighbours={1} 
                                            onPageChanged={this.onPageChanged}

                                        />
                                    </div>
                                </div>
                                { chemItems.map(chemItem => <ChemItem 
                                    key={chemItem.id}
                                    chemItem={chemItem}

                                    />) }
                                </div>
                            </div>
                            <hr />
                        </div>
                    </div>
                </div>
            </div>
        )   
    }
}

const mapStateToProps = state => ({
    selectedLab: state.selectedLab
})

export default connect(mapStateToProps) (ChemItemDashboard)