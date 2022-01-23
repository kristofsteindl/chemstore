import React from 'react';
import Pagination from '../Pagination'
import ChemItem from './ChemItem'

function ChemItemContent(props) {
    const { chemItems, totalItems, currentPage, totalPages, onPageChanged } = props;
    const headerClass = ['text-dark py-2 pr-4 m-0', currentPage ? 'border-gray border-right' : ''].join(' ').trim();
    return (
        
        <div className="container mb-5">
            <div className="row d-flex flex-row py-5">
                <div className="w-100 px-4 py-5 d-flex flex-row flex-wrap align-items-center justify-content-between">
                    <div className="d-flex flex-row align-items-center">
                        <h2 className={headerClass}>
                            <strong className="text-secondary">{totalItems}</strong> Registered Chemicals
                        </h2>
                    </div>
                    { currentPage && (
                        <span className="current-page d-inline-block h-100 pl-4 text-secondary">
                        Page <span className="font-weight-bold">{ currentPage }</span> / <span className="font-weight-bold">{ totalPages }</span>
                        </span>
                    ) }
                    
                    
                    <div className="d-flex flex-row py-4 align-items-center">
                        <Pagination 
                            totalRecords={totalItems} 
                            pageLimit={10} 
                            pageNeighbours={1} 
                            onPageChanged={onPageChanged}

                        />
                    </div>
                </div>
                { chemItems.map(chemItem => 
                        <ChemItem
                            key={chemItem.id}
                            chemItem={chemItem}
                        />
                    ) 
                }
            </div>
        </div>
    );
}

export default ChemItemContent
